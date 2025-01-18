package ch.heigvd.dai.controllers;


import ch.heigvd.dai.models.User;
import io.javalin.http.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class UserController {
    private final ConcurrentHashMap<Integer, User> users;
    private final AtomicInteger userId = new AtomicInteger(1);

    public UserController(ConcurrentHashMap<Integer, User> users) {
        this.users = users;
    }

    public void create(Context ctx) {
        User newUser = ctx.bodyValidator(User.class).check(obj -> obj.firstName != null, "Missing first name")
                .check(obj -> obj.lastName != null, "Missing last name")
                .check(obj -> obj.email != null, "Missing email")
                .check(obj -> obj.password != null, "Missing password")
                .get();

        for (User user : users.values()) {
            if (user.email.equalsIgnoreCase(newUser.email)) {
                throw new ConflictResponse();
            }
        }

        User user = new User();

        user.id = userId.getAndIncrement();
        user.firstName = newUser.firstName;
        user.lastName = newUser.lastName;
        user.email = newUser.email;
        user.password = newUser.password;

        users.put(user.id, user);

        ctx.status(HttpStatus.CREATED);
        ctx.json(user);
    }

    public void getOne(Context ctx) {
        Integer id = ctx.pathParamAsClass("id", Integer.class).get();

        User user = users.get(id);

        if (user == null) {
            throw new NotFoundResponse();
        }

        ctx.json(user);
    }

    public void getMany(Context ctx) {
        String firstName = ctx.queryParam("firstName");
        String lastName = ctx.queryParam("lastName");

        List<User> users = new ArrayList<>();

        for (User user : this.users.values()) {
            if (firstName != null && !user.firstName.equalsIgnoreCase(firstName)) {
                continue;
            }

            if (lastName != null && !user.lastName.equalsIgnoreCase(lastName)) {
                continue;
            }

            users.add(user);
        }

        ctx.json(users);
    }

    public void update(Context ctx) {
        Integer id = ctx.pathParamAsClass("id", Integer.class).get();

        User updateUser =
                ctx.bodyValidator(User.class)
                        .check(obj -> obj.firstName != null, "Missing first name")
                        .check(obj -> obj.lastName != null, "Missing last name")
                        .check(obj -> obj.email != null, "Missing email")
                        .check(obj -> obj.password != null, "Missing password")
                        .get();

        User user = users.get(id);

        if (user == null) {
            throw new NotFoundResponse();
        }

        user.firstName = updateUser.firstName;
        user.lastName = updateUser.lastName;
        user.email = updateUser.email;
        user.password = updateUser.password;

        users.put(id, user);

        ctx.json(user);
    }

    public void delete(Context ctx) {
        Integer id = ctx.pathParamAsClass("id", Integer.class).get();

        if (!users.containsKey(id)) {
            throw new NotFoundResponse();
        }

        users.remove(id);

        ctx.status(HttpStatus.NO_CONTENT);
    }
}


