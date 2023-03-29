package gbsw.plutter.project.outing.controller;

import gbsw.plutter.project.outing.service.SignService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sign")
public class SignController {
    private SignService signService;

    @PostMapping("/in")
    public ResponseEntity<String> writeReview() {
        return ResponseEntity.ok().body("리뷰 등록이 완료되었습니다.");
    }
}
