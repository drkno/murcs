package sws.murcs.exceptions;

public class ImperialException extends RuntimeException {

    private static final String march = "Dion Vader!! DA DA DA, DUN DADAA DUN DADAA, DEIN DEIN DEIN, DEIN DUDUN, DEIN DUDUN, DIN DUN DINDIN DEIN DUN DUNDUNDUN... Oh FUCK IT!";

    @Override
    public String getMessage() {
        return march;
    }
}
