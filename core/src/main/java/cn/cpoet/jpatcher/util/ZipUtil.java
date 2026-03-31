package cn.cpoet.jpatcher.util;

import cn.cpoet.jpatcher.exception.JPatcherException;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * zip压缩文件处理工具
 *
 * @author CPoet
 */
public abstract class ZipUtil {
    private ZipUtil() {
    }

    public static void writeEntry(ZipOutputStream out, ZipEntry entry, VirtualFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] bytes = FileUtil.loadBytes(inputStream);
            writeEntry(out, entry, bytes);
        } catch (Exception e) {
            throw new JPatcherException("Read file Or Write Zip entry fail", e);
        }
    }

    public static void writeEntry(ZipOutputStream out, ZipEntry entry, byte[] data) {
        try {
            entry.setSize(data.length);
            out.putNextEntry(entry);
            out.write(data);
        } catch (Exception e) {
            throw new JPatcherException("Write Zip entry fail", e);
        }
    }
}
