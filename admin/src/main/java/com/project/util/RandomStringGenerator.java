package com.project.util;

import java.util.Random;

/**
 * 六位随机字符串生成器
 */
public class RandomStringGenerator {
    // 定义生成随机字符串的方法
    public static String generateSixCharacterRandomString() {
        // 定义随机字符的集合，包括数字和字母
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // 循环六次，每次从字符集合中随机选择一个字符，并添加到StringBuilder中
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }
    }


