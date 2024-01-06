package ua.mai.library.model;

import java.net.UnknownServiceException;
import java.util.Arrays;
import java.util.Optional;

public enum Gender {
    MALE('M'),
    FEMALE('F')
    ;

    char constant;

    Gender(char constant) {
        this.constant = constant;
    }

    public char getConstant() {
        return constant;
    }

    public static Gender constantOf(Character constant) {
        if (constant == null)
            return null;
        Optional<Gender> genderOptional = Arrays.stream(Gender.values()).filter(gender -> constant.equals(gender.getConstant())).findFirst();
        return genderOptional.orElseThrow(() -> new IllegalArgumentException("Unknown Gender enum constant: '" + constant + "'"));
    }

}
