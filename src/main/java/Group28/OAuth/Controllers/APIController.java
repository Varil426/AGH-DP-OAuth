package Group28.OAuth.Controllers;

import Group28.OAuth.Model.*;
import Group28.OAuth.Model.State.AuthenticatingClient;
import Group28.OAuth.Model.State.Context;
import Group28.OAuth.Model.State.Response;
import Group28.OAuth.View.APIView;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/api")
public class APIController {
    //never used (?)
    private AuthenticatingClient model;
    private APIView view;

    //TODO: odkomentowane wyrzuca błąd
    //Nie mam pojęcia dlaczego, to tylko konstruktor /Gosia
//    public APIController(AuthenticatingClient model, APIView view) {
//        this.model = model;
//        this.view = view;
//    }

    @GetMapping("/validateToken")
    public @ResponseBody
    String validateToken(@RequestParam String clientID, @RequestParam String accessToken) throws SQLException {

        ValidateToken validateToken = new ValidateToken();
        boolean response = validateToken.validateToken(Long.parseLong(clientID), accessToken);
        String result = view.validToken(response);
        /* funkcja zwraca false gdy:
            - minął expiration time
            - nie ma tokenu o takich parametrach
         */
        return result;
    }

    //TODO to chyba nie ma szansy działać \Gosia
    @GetMapping("/createToken")
    public @ResponseBody
    String createToken(@RequestParam String clientID, @RequestParam String authCode, HttpServletResponse httpServletResponse) throws SQLException {
        Map<String, String> params = new HashMap<>();
        params.put("clientID", clientID);
        params.put("code", authCode);
        Context context = new Context();
        context.changeState(new AuthenticatingClient());
        Response response = context.handle(params);
        // response.content to obiekt AuthCode
        view.createToken(response, httpServletResponse);
        return "ok";
    }

    @GetMapping("/refreshToken")
    public @ResponseBody
    String refreshToken(@RequestParam String clientID, @RequestParam String refreshToken, HttpServletResponse httpServletResponse) throws SQLException {
        Map<String, String> params = new HashMap<>();
        params.put("clientID", clientID);
        params.put("refreshToken", refreshToken);

        Context context = new Context();
        context.changeState(new AuthenticatingClient());
        Response response = context.handle(params);
        //wywolanie view - ustawienie ciastek
        view.refreshToken(response, httpServletResponse);
        //a to po co? /Gosia
        // response.content to String[] - [accessToken, refreshToken]
        return "ok";
    }

    @GetMapping("/revokeToken")
    public @ResponseBody
    String revokeToken(@RequestParam String clientID, @RequestParam String accessToken) throws SQLException {

        RevokeToken revokeToken = new RevokeToken();
        boolean response = revokeToken.revokeToken(Long.parseLong(clientID), accessToken);
        System.out.println(response);
        /* funkcja zwraca true gdy udało się zrobić revoke
           w przeciwnym przypadku wyrzuca Bad Request / IllegalStateException
         */
        //sztuka dla sztuki
        return view.revokeToken(response);
    }

    @GetMapping("/revokeGrantType")
    public @ResponseBody
    String revokeGrantType(@RequestParam String clientID, @RequestParam String authCode) throws SQLException {

        RevokeGrantType revokeGrantType = new RevokeGrantType();
        boolean response = revokeGrantType.revokeGrantType(Long.parseLong(clientID), authCode);
        System.out.println(response);

        /* funkcja zwraca true gdy udało się zrobić revoke
           w przeciwnym przypadku wyrzuca Bad Request
         */
        return view.revokeGrantType(response);
    }

    @GetMapping("/getUserData")
    public @ResponseBody
    JSONObject getUserData(@RequestParam String clientID, @RequestParam String accessToken) throws SQLException {

        GetUserData getUserData = new GetUserData();
        JSONObject userData = getUserData.getUserData(Long.parseLong(clientID), accessToken);
        System.out.println(userData);

        /* funkcja zwraca JSONObject gdy accessToken jest valid
           przykład: {"user_email":"slepianka@wp.pl2","user_username":"slepianka2"}
           scopes muszą być zdefinowane w bazie: user_birthdate, user_email, user_firstname, user_phonenumber, user_surname, user_username
           w przeciwnym przypadku wyrzuca Bad Request
         */
        return userData;
    }
}
