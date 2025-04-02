package web.controller;

import web.model.User;
import web.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public String allUsers(Model model) {
		List<User> users = userService.findAll();
		model.addAttribute("users", users);
		return "user/user-list";
	}

	@GetMapping("/new")
	public String createUserForm(@ModelAttribute("user") User user) {
		System.out.println("new user");
		return "user/user-create";
	}

	@PostMapping
	public String createUser(@ModelAttribute("user") @Valid User user,
							 BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "user/user-create";
		}

		userService.save(user);
		return "redirect:/users";
	}

	@GetMapping("/edit")
	public String editUserForm(@RequestParam("id") Long id, Model model) {
		Optional<User> userById = userService.findById(id);

		if (userById.isPresent()) {
			model.addAttribute("user", userById.get());
			return "user/edit-user";
		} else {
			return "redirect:/users";
		}
	}

	@PostMapping("/edit")
	public String editUser(@ModelAttribute("user") @Valid User user,
						   BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "user/edit-user";
		}

		userService.updateUser(user);
		return "redirect:/users";
	}

	@PostMapping("/delete")
	public String deleteUser(@RequestParam("id") Long id) {
		userService.deleteById(id);
		return "redirect:/users";
	}
}
