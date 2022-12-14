package za.co.wethinkcode.weshare.app;

import io.javalin.core.security.AccessManager;
import io.javalin.core.security.RouteRole;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import za.co.wethinkcode.weshare.login.LoginController;
import za.co.wethinkcode.weshare.app.model.Person;

import java.util.Set;

public class DefaultAccessManager implements AccessManager {
    @Override
    public void manage(@NotNull Handler handler, @NotNull Context context, @NotNull Set<RouteRole> set) throws Exception {
        // Get the user (which is a Person object) from the session handler
        Person currentPerson = context.sessionAttribute("user");

        // If there is no user in the session handler and this is not the login URL
        // then redirect the user to the login page (/index.html)
        // or just let the current handler continue processing the request.
        if (currentPerson == null && !context.path().equals("/login")) {
            redirectToLogin(context);
        } else {
            handler.handle(context);
        }
    }

    private static void redirectToLogin(Context ctx) {
        ctx.redirect(LoginController.ROOT_PATH);
    }
}