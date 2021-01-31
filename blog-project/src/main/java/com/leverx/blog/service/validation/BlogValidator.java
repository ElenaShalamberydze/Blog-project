package com.leverx.blog.service.validation;

import com.leverx.blog.exception.ValidatorInnerException;
import com.leverx.blog.model.Article;
import com.leverx.blog.model.Comment;
import com.leverx.blog.model.User;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlogValidator {

    private static final Logger logger = Logger.getLogger(BlogValidator.class);

    private static List<String> words;

    static {
        try (Scanner scanner =
                     new Scanner(Paths.get(BlogValidator.class.getResource("/blackList.txt").toURI()).toFile())
        ) {
            words = new ArrayList<>();
            while (scanner.hasNextLine()) {
                words.add(scanner.nextLine());
            }
        } catch (FileNotFoundException | URISyntaxException e) {
            logger.error("Internal error during validation process");
            throw new ValidatorInnerException("Validator inner error!");
        }
    }

    public static boolean isCorrect(Article article) {
        return fieldCheck(article.getTitle()) && fieldCheck(article.getText());
    }

    public static boolean isCorrect(User user) {
        return fieldCheck(user.getFirstName()) && fieldCheck(user.getLastName());
    }

    public static boolean isCorrect(Comment comment) {
        return fieldCheck(comment.getMessage());
    }


    public static boolean tagsCheck(String tagString) {
        if (null != tagString && !tagString.isEmpty()) {
            String[] tags = tagString.split(",");
            for (String tag : tags) {
                if (!fieldCheck(tag)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean fieldCheck(String fieldName) {
        return (null == fieldName || fieldName.isEmpty() || isEthical(fieldName));
    }


    private static boolean isEthical(String fieldName) {
        for (String word : words) {
            Pattern pattern = Pattern.compile(word);
            Matcher matcher = pattern.matcher(fieldName);
            if (matcher.find()) {
                return false;
            }
        }
        return true;
    }
}
