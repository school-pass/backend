package gbsw.plutter.project.PMS.controller.schoolTime;

import gbsw.plutter.project.PMS.dto.STDTO;
import gbsw.plutter.project.PMS.model.SchoolTime;
import gbsw.plutter.project.PMS.service.admin.AdminService;
import gbsw.plutter.project.PMS.service.schoolTime.TimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/time")
@RequiredArgsConstructor
public class TimeController {
    private final AdminService adminService;
    private final TimeService timeService;
    @GetMapping("")
    public List<SchoolTime> getAllTime() {
        try {
            return adminService.getAllTime();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }
    }

    @PostMapping("/period")
    public SchoolTime findTimeByPeriod(@RequestBody STDTO stdto) {
        try {
            return timeService.findTimeByPeriod(stdto.getPeriod());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }
    }
}
