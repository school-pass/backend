package gbsw.plutter.project.PMS.controller.user;

import gbsw.plutter.project.PMS.dto.MemberDTO;
import gbsw.plutter.project.PMS.model.Authority;
import gbsw.plutter.project.PMS.model.Member;
import gbsw.plutter.project.PMS.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final AdminService adminService;
    @PostMapping("/findById")
    public Map<String, Object> getMemberById(@RequestBody MemberDTO md) {
        Member member = adminService.getMemberById(md.getId());
        Map<String, Object> modedMember = new HashMap<>();
        try {
            modedMember.put("id", member.getId());
            modedMember.put("name", member.getName());
            modedMember.put("roles", getRolesAsString(member.getAuthorities())); // 수정된 부분
            modedMember.put("account", member.getAccount());
            modedMember.put("serialNum", member.getSerialNumber());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "잘못된 요청입니다.");
        }
        return modedMember;
    }

    private List<String> getRolesAsString(List<Authority> authorities) { // 추가된 메서드
        List<String> roles = new ArrayList<>();
        for (Authority authority : authorities) {
            roles.add(authority.getName());
        }
        return roles;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> findAllUsers() {
        try {
            List<Member> memberList = adminService.findAllUsers();
            List<Map<String, Object>> modedMemberList = new ArrayList<>();
            for (Member member : memberList) {
                Map<String, Object> modifiedList = new HashMap<>();
                modifiedList.put("id", member.getId());
                modifiedList.put("name", member.getName());

                // Extracting the role names
                List<String> roleNames = member.getAuthorities().stream()
                        .map(authority -> authority.getName())
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }
    }

}
