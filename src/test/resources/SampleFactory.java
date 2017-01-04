import java.lang.String;

public final class SampleFactory {
  public static Sample create(String _name, String _desc) {
    return new Sample(_name,_desc);
  }
}
