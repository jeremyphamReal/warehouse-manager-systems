package com.jeremy.warehouse.service;

import com.jeremy.warehouse.models.DTO.UserCreateRequest;
import com.jeremy.warehouse.models.DTO.UserResponse;
import com.jeremy.warehouse.models.DTO.UserRoleUpdateRequest;
import com.jeremy.warehouse.models.User.Role;
import com.jeremy.warehouse.models.User.User;
import com.jeremy.warehouse.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
//QUY UOC: [method]_should[ExpectedBehavior]_when[Condition]
public class UserServiceTest {
    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    //TODO: createUser - throw khi username da ton tai
    @Test
    public void createUser_shouldThrow_whenUsernameAlreadyExists() {
        UserCreateRequest request = new UserCreateRequest(
                "binh",
                "rawPassword",
                Role.STAFF
        );

        when(userRepo.existsByUsername("binh")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(request)
        );

        assertEquals("Username is already in use", ex.getMessage());

        verify(userRepo).existsByUsername("binh");
        verify(userRepo, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }
//TODO: createUser - throw khi username rong/null
    @Test
    public void createUser_shouldThrow_whenUsernameIsNull() {
        UserCreateRequest request = new UserCreateRequest(null, "123", Role.STAFF);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(request));

        assertEquals("Username can not be empty", ex.getMessage());

        verify(userRepo, never()).existsByUsername(anyString());
        verify(userRepo, never()).existsByUsername(isNull());
        verify(userRepo, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }
//TODO: createUser - thanh cong -> password duoc hash (khong luu plain text), tra ve UserResponse KHONG co password
    @Test
    public void createUser_shouldSuccess_whenPasswordHaveHash(){
        String rawPassword = "123";
        String hashPassword = "$2a$12$FkxV4Uk4At5N2qREw1uuY.hnWMzcAQDZdOZTBi2kJkYNp7WUcJPhO";

        UserCreateRequest request = new UserCreateRequest("binh",  rawPassword, Role.STAFF);

        when(userRepo.existsByUsername("binh")).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(hashPassword);
        when(userRepo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(100L);
            return u;
        });

        UserResponse response = userService.createUser(request);
        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals("binh", response.username());
        assertEquals(Role.STAFF, response.role());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class); //Bắt User object truyền vào save()
        verify(userRepo).save(captor.capture());

        User savedUser = captor.getValue();

        //Password phải là hash, KHÔNG phải plain text
        assertEquals(hashPassword, savedUser.getPassword());
        assertNotEquals(rawPassword, savedUser.getPassword());
        verify(passwordEncoder).encode(rawPassword);

        //Encoder phải được gọi với raw password
        verify(userRepo, times(1)).save(any(User.class));
    }
//TODO: createUser - role null -> mac dinh STAFF (neu ap dung)
    @Test
    public void createUser_shouldDefaultToStaff_whenRoleIsNull(){
        String rawPassword = "123456";
        String hashedPassword = "$2a$10$mockedHashValue";
        UserCreateRequest request = new UserCreateRequest("binh", rawPassword, null);

        when(userRepo.existsByUsername("binh")).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
        when(userRepo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(100L);
            return u;
        });

        UserResponse response = userService.createUser(request);

        assertEquals(Role.STAFF, response.role());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals(Role.STAFF, savedUser.getRole());
        assertEquals(hashedPassword, savedUser.getPassword());
        assertNotEquals(rawPassword, savedUser.getPassword());

        verify(passwordEncoder).encode(rawPassword);
    }
//TODO: updateUserRole - throw khi user KHONG ton tai
    @Test
    public void updateUserRole_shouldThrow_whenUsernameIsNotExists() {
        User existingUser = User.builder().id(1L).username("binh").role(Role.STAFF).build();
        UserRoleUpdateRequest request = new UserRoleUpdateRequest(Role.ADMIN);

        when(userRepo.findById(existingUser.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.updateUserRole(1L,request));

        assertTrue(exception.getMessage().contains("User not found with id: " + existingUser.getId()));

        verify(userRepo).findById(existingUser.getId());
        verify(userRepo, never()).save(any(User.class));
    }
//TODO: updateUserRole - throw -> khi userId khong ton tai
    @Test
    public void updateUserRole_shouldThrow_whenUserNotFound() {
        Long userId = 999L;
        User existingUser = User.builder().id(userId).role(Role.STAFF).build();
        UserRoleUpdateRequest request = new UserRoleUpdateRequest(Role.ADMIN);

        when(userRepo.findById(existingUser.getId())).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUserRole(existingUser.getId(), request)
        );

        assertTrue(ex.getMessage().contains("not found")
                || ex.getMessage().contains("User"));
        verify(userRepo, never()).save(any(User.class));
    }
//TODO: updateUserRole - throw khi role null
    @Test
    public void updateUserRole_shouldThrow_whenRoleIsNull() {
        User existingUser = User.builder().id(1L).username("binh").role(Role.STAFF).build();

        UserRoleUpdateRequest request = new UserRoleUpdateRequest(null);

        when(userRepo.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.updateUserRole(1L,request));

        assertTrue(exception.getMessage().contains("Role can not blank"));

        verify(userRepo, never()).save(any(User.class));
    }
//TODO: updateUserRole - thanh cong -> tra ve dung role moi
    @Test
    public void updateUserRole_shouldSuccess_whenHaveRole(){
        User existingUser = User.builder().id(1L).username("binh").role(Role.STAFF).build();

        UserRoleUpdateRequest request = new UserRoleUpdateRequest(Role.ADMIN);

        when(userRepo.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
        when(userRepo.save(any(User.class))).thenAnswer(invocation ->  invocation.getArgument(0));

        UserResponse response = userService.updateUserRole(1L,request);

        assertNotNull(response);
        assertEquals(existingUser.getId(), response.id());
        assertEquals("binh", response.username());
        assertEquals(Role.ADMIN, response.role());

        verify(userRepo).findById(existingUser.getId());
        verify(userRepo, times(1)).save(any(User.class));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(captor.capture());
        assertEquals(Role.ADMIN, captor.getValue().getRole());
    }
    //TODO: updateUserRole - thanh cong -> khi khong co thay doi role tu request
    @Test
    public void updateUserRole_shouldNotSave_whenRoleIsUnchanged() {
        User existingUser = User.builder().id(1L).role(Role.ADMIN).build();
        UserRoleUpdateRequest request = new UserRoleUpdateRequest(Role.ADMIN);  // giống role cũ

        when(userRepo.findById(1L)).thenReturn(Optional.of(existingUser));

        userService.updateUserRole(1L, request);

        verify(userRepo, never()).save(any(User.class));   // ← skip save
    }
}
