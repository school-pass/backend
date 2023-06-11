package gbsw.plutter.project.PMS.controller.schoolTime;

import gbsw.plutter.project.PMS.dto.STDTO;
import gbsw.plutter.project.PMS.model.SchoolTime;
import gbsw.plutter.project.PMS.service.admin.AdminService;
import gbsw.plutter.project.PMS.service.schoolTime.TimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/time")
@RequiredArgsConstructor
public class TimeController {
    private final AdminService adminService;
    private final TimeService timeService;
    @GetMapping("")
    public List<SchoolTime> getAllTime() throws Exception {
        return adminService.getAllTime();
    }

    @PostMapping("/period")
    public SchoolTime findTimeByPeriod(STDTO stdto) throws Exception {
        try {
            return timeService.findTimeByPeriod(stdto);
        } catch (Exception e) {
            throw new Exception("findTimeByPeriod에서 오류 발생");
        }
    }
}
