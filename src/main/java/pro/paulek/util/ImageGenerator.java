package pro.paulek.util;

import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class ImageGenerator {

    private final static Logger logger = LoggerFactory.getLogger(ImageGenerator.class);
    //private final static int AVATAR_SIZE = 256;
    private final static int AVATAR_SIZE = 275;

    public ImageGenerator() {}

    public File generateWelcomeImage(User user, String welcome) {
        var image = this.generateWelcomeImage(user.getName(), user.getAsTag().split("#")[1], welcome, user.getEffectiveAvatarUrl());
        try {
            var tempFile = File.createTempFile("welcome", ".png");

            ImageIO.write(image, "png", tempFile);

            return tempFile;
        } catch (Exception exception){
            logger.error("Cannot generate welcome image", exception);
        }
        return null;
    }

    public File generateLeaveImage(User user, String leave) {
        var image = this.generateLeaveImage(user.getName(), user.getAsTag().split("#")[1], leave, user.getEffectiveAvatarUrl());
        try {
            var tempFile = File.createTempFile("leave", ".png");

            ImageIO.write(image, "png", tempFile);

            return tempFile;
        } catch (Exception exception){
            logger.error("Cannot generate leave image", exception);
        }
        return null;
    }

    public BufferedImage generateLeaveImage(String nickname, String tag, String leaveMessage, String avatarUrl) {
        BufferedImage leaveImage = null;
        try {
            leaveImage = ImageIO.read(Objects.requireNonNull(ImageGenerator.class.getClassLoader().getResource("leave.png")));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        Graphics graphics = leaveImage.getGraphics();

        BufferedImage avatarImage = null;

        try
        {
            URL url = new URL(avatarUrl);
            avatarImage = ImageIO.read(url);
        } catch (IOException exception) {
            logger.warn("Cannot get avatar image for welcome message url: " + avatarUrl, exception);
        }

        if (avatarImage == null) {
            try {
                avatarImage = ImageIO.read(Objects.requireNonNull(ImageGenerator.class.getClassLoader().getResource("discord-avatar.png")));
            } catch (Exception exception) {
                logger.error("Cannot read default avatar image for welcome message", exception);
                return null;
            }
        }

        //Generate discord avatar
        var avatar = this.generateRoundAvatar(avatarImage);
        var resizedAvatar = this.resizeImage(avatar, AVATAR_SIZE, AVATAR_SIZE);
        graphics.drawImage(resizedAvatar, 62, 64, null);

        //Generate nickname text
        this.drawStringFont(graphics, Font.BOLD, Color.white, 410, 175, 48, nickname);

        //Generate discord tag text
        this.drawStringFont(graphics, Font.BOLD, Color.white, 510, 245, 36, tag);

        //Generate footer welcome message
        this.drawStringFont(graphics, Font.PLAIN, Color.white, 410, 330, 36, leaveMessage);

        return leaveImage;
    }

    public BufferedImage generateWelcomeImage(String nickname, String tag, String welcomeMessage, String avatarUrl) {
        BufferedImage welcomeImage = null;
        try {
            welcomeImage = ImageIO.read(Objects.requireNonNull(ImageGenerator.class.getClassLoader().getResource("welcome.png")));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        Graphics graphics = welcomeImage.getGraphics();

        BufferedImage avatarImage = null;

        try
        {
            URL url = new URL(avatarUrl);
            avatarImage = ImageIO.read(url);
        } catch (IOException exception) {
            logger.warn("Cannot get avatar image for welcome message url: " + avatarUrl, exception);
        }

        if (avatarImage == null) {
            try {
                avatarImage = ImageIO.read(Objects.requireNonNull(ImageGenerator.class.getClassLoader().getResource("discord-avatar.png")));
            } catch (Exception exception) {
                logger.error("Cannot read default avatar image for welcome message", exception);
                return null;
            }
        }

        //Generate discord avatar
        var avatar = this.generateRoundAvatar(avatarImage);
        var resizedAvatar = this.resizeImage(avatar, AVATAR_SIZE, AVATAR_SIZE);
        graphics.drawImage(resizedAvatar, 62, 64, null);

        //Generate nickname text
        this.drawStringFont(graphics, Font.BOLD, Color.white, 410, 175, 48, nickname);

        //Generate discord tag text
        this.drawStringFont(graphics, Font.BOLD, Color.white, 510, 245, 36, tag);

        //Generate footer welcome message
        this.drawStringFont(graphics, Font.PLAIN, Color.white, 410, 330, 36, welcomeMessage);

        return welcomeImage;
    }

    public String getUserAvatarUrl(User user) {
        return user.getAvatarUrl();
    }

    private void drawStringFont(Graphics graphics, int style, Color color, int x, int y, int size, String text) {
        graphics.setColor(color);
        graphics.setFont(new Font("Strenuous", style, size));
        graphics.drawString(text, x, y);
    }

    private BufferedImage generateRoundAvatar(BufferedImage bufferedImage) {
        assert bufferedImage != null;
        var diameter = Math.min(bufferedImage.getWidth(), bufferedImage.getHeight());
        var mask = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),  BufferedImage.TYPE_INT_ARGB);
        var g2d = mask.createGraphics();
        g2d.fillOval(0, 0, diameter - 1, diameter - 1);
        g2d.dispose();

        var masked = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        g2d = masked.createGraphics();
        var x = (diameter - bufferedImage.getWidth()) / 2;
        var y = (diameter - bufferedImage.getHeight()) / 2;
        g2d.drawImage(bufferedImage, x, y, null);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
        g2d.drawImage(mask, 0,0, null);
        g2d.dispose();

        return masked;
    }

    private BufferedImage resizeImage(BufferedImage bufferedImage, int width, int height) {
        Image resultingImage = bufferedImage.getScaledInstance(AVATAR_SIZE, AVATAR_SIZE, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(AVATAR_SIZE, AVATAR_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = resizedImage.getGraphics();
        graphics.drawImage(resultingImage, 0, 0, null);

        return resizedImage;
    }
}
