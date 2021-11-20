package com.example.demo.persistence;

import com.example.demo.model.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

//AOP를 통해 interface여도 작동한다.
@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, String> {
    //구현부 ?1은 메서드의 매개변수의 순서 위치
    @Query("select t from TodoEntity t where t.userId = ?1")
    List<TodoEntity> findByUserId(String userId);
}
