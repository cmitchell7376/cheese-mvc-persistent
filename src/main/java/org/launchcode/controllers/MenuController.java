package org.launchcode.controllers;

import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {
    @Autowired
    MenuDao menuDao;

    @Autowired
    CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model){
        model.addAttribute("menus",menuDao.findAll());
        model.addAttribute("title", "Menus");
        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String addMenuForm(Model model){
        model.addAttribute(new Menu());
        model.addAttribute("title", "Add Menu");
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddForm(@ModelAttribute @Valid Menu newMenu, Errors errors,Model model){

        if(errors.hasErrors()){
            model.addAttribute("title", "Menus");
            return "menu/add";
        }

        menuDao.save(newMenu);
        return "redirect:view/" + newMenu.getId();
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(@PathVariable int menuId, Model model){
        model.addAttribute("menu",menuDao.findOne(menuId));
        model.addAttribute("title", menuDao.findOne(menuId).getName());
        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int menuId){

        AddMenuItemForm form = new AddMenuItemForm(menuDao.findOne(menuId),cheeseDao.findAll());

        model.addAttribute("form",form);
        model.addAttribute("title", "Add item to menu:" + menuDao.findOne(menuId).getName());
        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String additem(@ModelAttribute @Valid AddMenuItemForm form, Errors errors, Model model){

        if(errors.hasErrors()){
            model.addAttribute("form",form);
            return "menu/add-item";
        }

        Menu menu = menuDao.findOne(form.getMenuId());
        menu.addItem(cheeseDao.findOne(form.getCheeseId()));
        menuDao.save(menu);

        return "redirect:/menu/view/" + menu.getId();
    }
}
