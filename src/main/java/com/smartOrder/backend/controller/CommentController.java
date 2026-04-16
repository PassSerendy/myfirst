package com.smartOrder.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartOrder.backend.common.Result;
import com.smartOrder.backend.entity.Comment;
import com.smartOrder.backend.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 提交评价
    @PostMapping
    public Result<String> submit(@RequestBody Comment comment, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        comment.setUserId(userId);
        commentService.save(comment);
        return Result.success("评价成功");
    }

    // 根据菜品ID获取评价列表（商家或用户看某个菜品的评价）
    @GetMapping("/dish/{dishId}")
    public Result<List<Comment>> getByDishId(@PathVariable Long dishId) {
        List<Comment> list = commentService.list(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getDishId, dishId)
                .orderByDesc(Comment::getCreateTime));
        return Result.success(list);
    }
}
