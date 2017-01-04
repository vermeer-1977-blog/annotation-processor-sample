package com.mycompany.annotationprocessor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"*"})
public class MakeFactoryProcessor extends AbstractProcessor {

    private Filer _filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        processingEnv.getMessager().printMessage(Kind.NOTE, "プロセッサー初期化");
        _filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        try {
            for (TypeElement typeElement : annotations) {
                for (Element element : roundEnv.getElementsAnnotatedWith(typeElement)) {
                    TargetClass classAnno = element.getAnnotation(TargetClass.class);
                    if (classAnno != null) {
                        createFactoryClass(element);
                    }
                }
            }
            processingEnv.getMessager().printMessage(Kind.NOTE, "クラスファイル生成");
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void createFactoryClass(Element element) throws IOException {
        ArgumentInfo argInfo = getArgumentInfo(element);

        MethodSpec.Builder builder = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.get(element.asType()))
                .addStatement("return new $T(" + argInfo.join() + ")",
                              TypeName.get(element.asType()));
        for (String arg : argInfo.getArgNames()) {
            builder.addParameter(String.class, arg);
        }
        MethodSpec create = builder.build();

        String className = element.getSimpleName() + "Factory";
        TypeSpec factory = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(create)
                .build();

        JavaFile javaFile = JavaFile.builder(
                getPackageName(element), factory).
                build();

        //生成したソースを確認したいので、コンソールに直接出力してみる
        System.out.println();
        System.out.println(javaFile);

        javaFile.writeTo(_filer);
    }

    private String getPackageName(Element element) {
        List<String> packNames = new ArrayList<String>();
        Element packageElem = element.getEnclosingElement();
        while (packageElem != null) {
            String packName = packageElem.
                    getSimpleName().toString();
            packNames.add(packName);
            packageElem = packageElem.getEnclosingElement();
        }

        StringBuilder sb = new StringBuilder();
        for (int i = packNames.size() - 1; i >= 0; i--) {
            if (sb.length() > 0) {
                sb.append(".");
            }
            sb.append(packNames.get(i));
        }
        return sb.toString();
    }

    private ArgumentInfo getArgumentInfo(Element element) {
        ArgumentInfo argInfo = new ArgumentInfo();
        for (Element e : element.getEnclosedElements()) {
            if (e.getAnnotation(TargetField.class) != null) {
                argInfo.add(e.getSimpleName().toString());
            }
        }
        return argInfo;
    }

    private class ArgumentInfo {

        private List<String> _argNames = new ArrayList<String>();

        public void add(String argName) {
            _argNames.add(argName);
        }

        public String[] getArgNames() {
            return _argNames.toArray(new String[0]);
        }

        public String join() {
            StringBuilder sb = new StringBuilder();
            for (String argName : _argNames) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(argName);
            }
            return sb.toString();
        }
    }
}
