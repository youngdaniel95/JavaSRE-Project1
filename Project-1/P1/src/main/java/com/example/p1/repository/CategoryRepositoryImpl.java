package com.example.p1.repository;

import com.example.p1.exceptions.EtBadRequestException;
import com.example.p1.exceptions.EtResourceNotFoundException;
import com.example.p1.models.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private static final String SQL_CREATE = "INSERT INTO r_categories (category_id, user_id, title, description) VALUES (NEXTVAL('r_categories_seq'), ?, ?, ?)";
    private static final String SQL_FIND_BY_ID = "SELECT C.category_id, C.user_id, C.title, C.description, COALESCE(SUM(T.amount), 0) TOTAL_EXPENSE FROM r_transactions T RIGHT OUTER JOIN r_categories C ON C.category_id = T.category_id WHERE C.user_id = ? AND C.category_id = ? GROUP BY C.category_id";
    private static final String SQL_FIND_ALL = "SELECT C.category_id, C.user_id, C.title, C.description, COALESCE(SUM(T.amount), 0) TOTAL_EXPENSE FROM r_transactions T RIGHT OUTER JOIN r_categories C ON C.category_id = T.category_id WHERE C.user_id = ? GROUP BY C.category_id";
    private static final String SQL_UPDATE = "UPDATE r_categories SET TITLE = ?, description = ? WHERE user_id = ? AND category_id = ?";
    private static final String SQL_DELETE_CATEGORY = "DELETE FROM r_categories WHERE user_id = ? AND category_id = ?";
    private static final String SQL_DELETE_ALL_TRANSACTIONS = "DELETE FROM r_transactions WHERE category_id = ?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Integer create(Integer userId, String title, String description) throws EtBadRequestException {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, userId);
                ps.setString(2, title);
                ps.setString(3, description);
                return ps;
            }, keyHolder);
            return (Integer) keyHolder.getKeys().get("CATEGORY_ID");
        }catch (Exception e) {
            throw new EtBadRequestException("Invalid request");
        }
    }

    @Override
    public Category findById(Integer userId, Integer categoryId) throws EtResourceNotFoundException {
        try {
            return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, new Object[]{userId, categoryId}, categoryRowMapper);
        }catch (Exception e) {
            throw new EtResourceNotFoundException("Category not found");
        }
    }

    private RowMapper<Category> categoryRowMapper = ((rs, rowNum) -> {
        return new Category(rs.getInt("CATEGORY_ID"),
                rs.getInt("USER_ID"),
                rs.getString("TITLE"),
                rs.getString("DESCRIPTION"),
                rs.getDouble("TOTAL_EXPENSE"));
    });

    @Override
    public List<Category> findAll(Integer userId) throws EtResourceNotFoundException {
        return jdbcTemplate.query(SQL_FIND_ALL, new Object[]{userId}, categoryRowMapper);
    }


    @Override
    public void update(Integer userId, Integer categoryId, Category category) throws EtBadRequestException {
        try {
            jdbcTemplate.update(SQL_UPDATE, new Object[]{category.getTitle(), category.getDescription(), userId, categoryId});
        }catch (Exception e) {
            throw new EtBadRequestException("Invalid request");
        }
    }

    @Override
    public void removeById(Integer userId, Integer categoryId) {
        this.removeAllCatTransactions(categoryId);
        jdbcTemplate.update(SQL_DELETE_CATEGORY, new Object[]{userId, categoryId});
    }

    private void removeAllCatTransactions(Integer categoryId) {
        jdbcTemplate.update(SQL_DELETE_ALL_TRANSACTIONS, new Object[]{categoryId});
    }


}
