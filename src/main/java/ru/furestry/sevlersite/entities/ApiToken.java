package ru.furestry.sevlersite.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.zip.CRC32;

@Data
@AllArgsConstructor
public class ApiToken {

    private String value;

    private String hash;

    private final static String PREFIX = "svlr";

    public static String createToken(String crc32SecretKey) {
        SecureRandom secureRandom = new SecureRandom();
        String randomString = Base64.getEncoder().encodeToString(secureRandom.generateSeed(128));

        CRC32 crc32 = new CRC32();
        crc32.update(crc32SecretKey.getBytes());
        crc32.update(randomString.getBytes());

        return String.format(PREFIX + "_%s_%d", randomString, crc32.getValue());
    }

    public static boolean isValid(String token, String crc32SecretKey) {
        String[] tokenDecodedSplit = token.split("_");

        if (tokenDecodedSplit.length != 3 || tokenDecodedSplit[0] != PREFIX) {
            return false;
        }

        String randomString = tokenDecodedSplit[1];
        CRC32 crc32 = new CRC32();

        crc32.update(crc32SecretKey.getBytes());
        crc32.update(randomString.getBytes());

        if (!tokenDecodedSplit[2].equals(crc32)) {
            return false;
        }

        return true;
    }

    public static String hashToken(String apiTokenValue) {
        return new DigestUtils("SHA3-256").digestAsHex(apiTokenValue);
    }
}
