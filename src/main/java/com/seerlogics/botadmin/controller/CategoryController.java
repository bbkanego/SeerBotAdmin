package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.seerlogics.botadmin.service.CategoryService;
import com.seerlogics.commons.model.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(value = "/api/v1/category")
public class CategoryController extends BaseController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping(value = {"", "/",})
    public ResponseEntity<Boolean> save(@RequestBody Category category) {
        this.categoryService.save(category);
        return ResponseEntity.ok(true);
    }

    @GetMapping(value = {"", "/",})
    public ResponseEntity<Collection<Category>> getAll() {
        return ResponseEntity.ok(this.categoryService.getAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Category> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.categoryService.getSingle(id));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        this.categoryService.delete(id);
        return returnSuccessResponse();
    }

    @GetMapping(value = {"/init"})
    public ResponseEntity<Category> initModel() {
        return ResponseEntity.ok(this.categoryService.initModel());
    }

    @GetMapping(value = {"/get-for-edit"})
    public ResponseEntity<Collection<Category>> getForEdit() {
        return ResponseEntity.ok(this.categoryService.findForEdit());
    }
}
