package gbsw.plutter.project.PMS.controller.user;

import gbsw.plutter.project.PMS.model.Member;
import gbsw.plutter.project.PMS.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final AdminService adminService;
    @GetMapping("/{id}")
    public Optional<Member> getMemberById(@PathVariable("id") Long id) throws Exception {
        return adminService.getMemberById(id);
    }
    @GetMapping("/userList")
    public ResponseEntity<List<Map<String, Object>>> userList() throws Exception {
        try {
            List<Member> memberList = adminService.getUserList();
            List<Map<String, Object>> modedMemberList = new ArrayList<>();
            for (Member member : memberList) {
                Map<String, Object> modifiedList = new HashMap<>();
                modifiedList.put("id", member.getId());
                modifiedList.put("name", member.getName());

                // Extracting the role names
                List<String> roleNames = member.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toList());

                if (!roleNames.isEmpty()) {
                    modifiedList.put("role", roleNames.get(0));
                } else {
                    modifiedList.put("role", null);
                }

                modifiedList.put("account", member.getAccount());
                modifiedList.put("serialNum", member.getSerialNumber());
                modedMemberList.add(modifiedList);
            }
            return new ResponseEntity<>(modedMemberList, HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception("에러 발생");
        }
    }
}
