package journeybuddy.spring.service.user;

import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.web.dto.user.UserRequestDTO;

public interface UserCommandService {
    public User updateUser(UserRequestDTO.UpdateDTO request,String userEmail);
    public User addUser(UserRequestDTO.RegisterDTO request);
    User getUserById(Long id);
    User deletedById(Long id);
    public Long loginCheck(UserRequestDTO.RegisterDTO request);
    public boolean EmailDuplicationCheck(UserRequestDTO.RegisterDTO request);
    String getUserEmailById(Long id);
    User getUserByEmail(String userEmail);
}
