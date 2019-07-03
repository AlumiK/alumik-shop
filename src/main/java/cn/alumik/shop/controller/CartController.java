package cn.alumik.shop.controller;

import cn.alumik.shop.entity.Cart;
import cn.alumik.shop.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

@Controller
@RequestMapping("/cart")
public class CartController {

    private CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/index")
    public String actionGetGoods(Model model) {
        Set<Cart> carts = cartService.findAll();
        model.addAttribute("carts", carts);
        return "cart/cart";
    }

    @PostMapping("/delete")
    public String actionDeleteGood(Model model, int id) {
        cartService.delete(id);
        return "redirect:/cart/index";
    }
}