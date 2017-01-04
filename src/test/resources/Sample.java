
import com.mycompany.annotationprocessor.TargetClass;
import com.mycompany.annotationprocessor.TargetField;

@TargetClass
public class Sample {

    @TargetField
    private final String _name;

    @TargetField
    private final String _desc;

    public Sample(String name, String desc) {
        _name = name;
        _desc = desc;
    }

    public String getName() {
        return _name;
    }

    public String getDesc() {
        return _desc;
    }
}
