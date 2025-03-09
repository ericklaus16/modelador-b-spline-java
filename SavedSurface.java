import java.io.Serializable;

public class SavedSurface implements Serializable{
    private static final long serialVersionUID = 1L;
    
    public Surface surface;
    public Settings settings;
    public String name;
}
