package com.pract.mapper;

import com.pract.domain.Book;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookMapper {
    public List<Book> getAll();
}
