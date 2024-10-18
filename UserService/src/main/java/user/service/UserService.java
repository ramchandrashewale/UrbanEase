package user.service;

import user.dto.UpdatePassword;
import user.dto.UserRequest;
import user.dto.UserResponse;
import user.dto.UserUpdateRequest;


public interface UserService {

    UserResponse registerUser(UserRequest userRequest);

    UserResponse  getUser(String username);

    String changePassword(String userName, UpdatePassword updatePassword);

    String deleteUser(String username);

    UserResponse updateUser(UserUpdateRequest userRequest,String username);
}
