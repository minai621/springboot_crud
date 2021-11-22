package com.example.demo.controller;

import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.TodoDTO;
import com.example.demo.model.TodoEntity;
import com.example.demo.service.TodoService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController // 컨트롤러임
@RequestMapping("todo") // URI경로에 http메서드를 매핑
public class TodoController {

    @Autowired
    private TodoService service;

    @PostMapping
    public ResponseEntity<?> createTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
        try {
            // 1. TodoEntity로 객체 변환!
            TodoEntity entity = TodoDTO.toEntity(dto);

            // 2. id를 null로 초기화. 생성 당시엔 id가 없어야 함.
            entity.setId(null);

            // 3. 임시 사용자 아이디를 설정.
            entity.setUserId(userId);

            // 4. 서비스를 이용해 Todo 엔티티를 생성한다.
            List<TodoEntity> entities = service.create(entity);

            // 5. 자바 스트림을 이용해 리턴된 엔티티 리스트를 TOdoDTO 리스트로 변환한다.
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

            // 6. 변환된 TodoDTO를 이용해 ResponseDTO를 초기화
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

            // 7. ResponseDTO반환
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<?> retrieveTodoList(@AuthenticationPrincipal String userId) {
        // 1. 서비스 메서드 retrieve()를 이용해 Todo 리스트를 가져온다.
        List<TodoEntity> entities = service.retrieve(userId);

        // 2. 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO로 변환한다.
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

        // 3. 변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화한다.
        ResponseDTO<TodoDTO> responseDTO = ResponseDTO.<TodoDTO>builder().data(dtos).build();

        // 4. ResponseDTO를 반환한다.
        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping
    public ResponseEntity<?> updateTodo(@AuthenticationPrincipal String userId,@RequestBody TodoDTO dto) {
        // 1. dto를 entity로
        TodoEntity entity = TodoDTO.toEntity(dto);
        // 2. id를 초기화
        entity.setUserId(userId);
        // 3. entity를 업데이트
        List<TodoEntity> entities = service.update(entity);
        // 4. entity list를 TodoDTO로 변환
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
        // 5. ResponseDTO를 초기화
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
        try {
            TodoEntity entity = TodoDTO.toEntity(dto);

            entity.setUserId(userId);

            List<TodoEntity> entities = service.delete(entity);

            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }
}
