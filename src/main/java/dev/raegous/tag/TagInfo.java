package dev.raegous.tag;

import com.sun.org.apache.xpath.internal.objects.XString;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class TagInfo implements Serializable {
    private final String filePath = "UltimateTag.data";

    private String activeTaggerUsername;

    public String getActiveTaggerUsername() {
        return activeTaggerUsername;
    }

    public void setActiveTaggerUsername(String activeTaggerUsername) {
        this.activeTaggerUsername = activeTaggerUsername;
        saveData();
    }

    public boolean saveData() {
        try {
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filePath)));
            out.writeObject(this);
            out.close();
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public TagInfo loadData() {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(filePath)));
            TagInfo data = (TagInfo) in.readObject();
            in.close();
            return data;
        } catch (ClassNotFoundException | IOException e) {
            return null;
        }
    }
}
