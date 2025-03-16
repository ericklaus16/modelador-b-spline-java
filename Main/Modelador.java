package Main;
import Surface.*;
import IU.*;

public class Modelador {
    public static void main(String[] args) {
        Settings settings = new Settings();
        Surface superficie = new Surface(settings.m, settings.n);
        Canvas canvas = new Canvas(settings.viewport.umax, settings.viewport.vmax, settings, superficie);
        canvas.iniciarComGerenciador();
    }
}
