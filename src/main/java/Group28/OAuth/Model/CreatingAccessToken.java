package Group28.OAuth.Model;

import java.util.Map;

public class CreatingAccessToken extends State {

    //Singleton
    private static CreatingAccessToken instance = new CreatingAccessToken();

    private CreatingAccessToken() {}

    public static CreatingAccessToken instance() {
        return instance;
    }

    //TODO: Business logic and state transition
    @Override
    public void updateState(Context context,  Map<String, String>params)
    {
//        System.out.println("CreatingAccessToken");
        //TODO: zmiana stanu
//        context.setCurrentState([nowy stan].instance());
    }

}