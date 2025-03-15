package Main;
import Surface.*;
import IU.*;

public class Modelador {
    public static void main(String[] args) {
        Settings settings = new Settings();
        Surface superficie = new Surface(settings.m, settings.n, 30, 40);
        Canvas canvas = new Canvas(settings.width, settings.height, settings, superficie);
        canvas.iniciarComGerenciador();
    }
}
