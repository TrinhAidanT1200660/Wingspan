
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import javax.imageio.ImageIO;

public interface ImageHandler {
    public static final HashMap<String, ImageObject> cache = new HashMap<>();
    public static final HashMap<String, HashSet<String>> groups = new HashMap<>();

    public static BufferedImage get(String imagePath) {
        return get(imagePath, "Global");
    }

    public static BufferedImage get(String imagePath, String group) {
        ImageObject imgObj = cache.get(imagePath);
        if (imgObj != null && imgObj.image != null) return imgObj.image;
        
        try {
            BufferedImage image = ImageIO.read(ImageHandler.class.getResource("/resources/" + imagePath));
            if (image != null) {
                imgObj = new ImageObject(image, "Global");
                cache.put(imagePath, imgObj);
                getGroup(group).add(imagePath);
            }
            return image;
        } catch (IOException e) {
            System.out.println("Couldn't load image from " + imagePath + ": " + e.getMessage());
            return null;
        }
    }

    public static void setGroup(String imagePath, String group) {
        ImageObject imgObj = cache.get(imagePath);
        if (imgObj != null) {
            getGroup(imgObj.group).remove(imagePath);
            imgObj.group = group;
        }
        getGroup(group).add(imagePath);
    }

    private static HashSet<String> getGroup(String group) {
        HashSet<String> items = groups.get(group);
        if (items == null) {
            items = new HashSet<>();
            groups.put(group, items);
        }
        return items;
    }

    public static void loadGroup(String group) {
        HashSet<String> items = getGroup(group);
        for (String path : items) {
            get(path, group);
        }
    }

    public static void loadGroup(String group, Runnable onDone) {
        loadGroup(group);
        if (onDone != null) onDone.run();
    }

    public static void clearGroupCache(String group) {
        HashSet<String> items = getGroup(group);
        for (String path : items) cache.remove(path);
    }
}

class ImageObject {
    public BufferedImage image;
    public String group;

    public ImageObject(BufferedImage image, String group) {
        this.image = image;
        this.group = group;
    }
}