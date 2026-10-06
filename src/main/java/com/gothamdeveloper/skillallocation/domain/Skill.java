package com.gothamdeveloper.skillallocation.domain;

import java.util.Arrays;
import java.util.Locale;

public enum Skill {

    JAVA("java"),
    C("c"),
    CPP("c++", "cpp", "c plus plus"),
    C_SHARP("c#", "csharp", "c sharp"),
    PYTHON("python"),
    JAVASCRIPT("javascript", "js"),
    TYPESCRIPT("typescript", "ts"),
    GO("go", "golang"),
    RUST("rust"),
    KOTLIN("kotlin"),
    SWIFT("swift"),
    PHP("php"),
    RUBY("ruby"),
    SQL("sql"),
    HTML("html"),
    CSS("css"),
    REACT("react", "react js", "react.js"),
    ANGULAR("angular"),
    VUE("vue"),
    SPRING_BOOT("spring boot", "springboot"),
    DOT_NET(".net", "dotnet", "dot net"),
    NODE_JS("node.js", "nodejs", "node js"),
    DOCKER("docker"),
    PODMAN("podman"),
    KUBERNETES("kubernetes", "k8s"),
    AWS("aws", "amazon web services");

    private final String   displayName;
    private final String[] aliases;

    Skill(String displayName, String... aliases) {
        this.displayName = displayName;
        this.aliases = aliases;
    }

    public static Skill from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Skill must not be blank.");
        }

        String normalizedValue = normalize(value);

        return Arrays.stream(values())
                     .filter(skill -> skill.matches(normalizedValue))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("Unsupported skill: " + value));
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private boolean matches(String normalizedValue) {
        if (normalize(displayName).equals(normalizedValue)) {
            return true;
        }

        return Arrays.stream(aliases).map(Skill::normalize).anyMatch(normalizedValue::equals);
    }

    public String getDisplayName() {
        return displayName;
    }

}