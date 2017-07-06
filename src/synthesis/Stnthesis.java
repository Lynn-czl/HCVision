package synthesis;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.sun.jna.Pointer;

import implementation.StitcherItf;

/**
 * 完成图片拼接
 * 
 * @author WSL
 *
 */
public class Stnthesis {

	/**
	 * 预处理拼接
	 * 
	 * @param path
	 *            待拼接图片的路径集合
	 */
	public static void process(String[] path) {
		if (path.length < 2) {
			System.out.println("We need more pictures!");
			return;
		}

		StitcherItf.instanceDll.stitchimg_from_path_to_path(path, path.length,
				"Results\\" + System.currentTimeMillis() + ".jpeg");
	}

	/**
	 * 预处理拼接
	 * 
	 * @param list
	 *            jpeg图片byte格式数组集合
	 */
	public static void process(List<byte[]> list) {
		if (list.size() < 2) {
			System.out.println("We need more pictures!");
			return;
		}

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat[] mats = new Mat[list.size()];
		int i = 0;

		Iterator<byte[]> it = list.iterator();
		while (it.hasNext()) {
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(it.next());
				BufferedImage bi = ImageIO.read(in);
				mats[i++] = bufferedImageToMat(bi);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Mat mat = stitchImg(mats);
		BufferedImage image = matToBufferedImage(mat);
		System.out.println("save to file");
		saveToFile(image);
		Stitcher s = new Stitcher();
		Imgproc.resize(mat, mat, new Size(640, 480));
		s.showImg(mat);
	}

	/**
	 * BufferedImage --> Mat
	 * 
	 * @param original
	 * @return
	 */
	public static Mat bufferedImageToMat(BufferedImage original) {
		byte[] pixels = ((DataBufferByte) original.getRaster().getDataBuffer()).getData();
		Mat mat = Mat.eye(original.getHeight(), original.getWidth(), CvType.CV_8UC3);
		mat.put(0, 0, pixels);
		return mat;
	}

	/**
	 * Mat --> BufferedImage
	 * 
	 * @param matrix
	 * @return
	 */
	public static BufferedImage matToBufferedImage(Mat matrix) {
		int cols = matrix.cols();
		int rows = matrix.rows();
		int elemSize = (int) matrix.elemSize();
		byte[] data = new byte[cols * rows * elemSize];
		int type;
		matrix.get(0, 0, data);
		switch (matrix.channels()) {
		case 1:
			type = BufferedImage.TYPE_BYTE_GRAY;
			break;
		case 3:
			type = BufferedImage.TYPE_3BYTE_BGR;
			byte b;
			for (int i = 0; i < data.length; i = i + 3) {
				b = data[i];
				data[i] = data[i + 2];
				data[i + 2] = b;
			}
			break;
		default:
			return null;
		}
		BufferedImage image2 = new BufferedImage(cols, rows, type);
		image2.getRaster().setDataElements(0, 0, cols, rows, data);
		return image2;
	}

	/**
	 * BufferedImage --> file
	 * 
	 * @param image2
	 */
	private static void saveToFile(BufferedImage image2) {
		try {
			ImageIO.write(image2, "jpeg", new File("Results\\" + System.currentTimeMillis() + ".jpeg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调用拼接器动态链接库 拼接多个Mat
	 * 
	 * @param mats
	 * @return 拼接后的Mat
	 */
	public static Mat stitchImg(Mat[] mats) {
		int size = mats.length;

		int[] mats_rows = new int[size];
		int[] mats_cols = new int[size];
		int[] mats_cvtype = new int[size];
		int mats_total = 0;
		for (int i = 0; i < size; i++) {
			mats_rows[i] = mats[i].rows();
			mats_cols[i] = mats[i].cols();
			mats_cvtype[i] = mats[i].type();
			mats_total += (mats[i].total() * mats[i].channels());
		}
		byte[] mats_data = new byte[mats_total];
		int current_offset = 0;
		for (int i = 0; i < size; i++) {
			byte[] img_data = new byte[(int) mats[i].total() * mats[i].channels()];
			mats[i].get(0, 0, img_data);
			for (int j = 0; j < img_data.length; j++) {
				mats_data[current_offset + j] = img_data[j];
			}
			current_offset += img_data.length;
		}

		int mat_rows[] = { 0 };
		int mat_cols[] = { 0 };
		int mat_cvtype[] = { 0 };
		Pointer p_data = StitcherItf.instanceDll.stitchimg_from_mats_to_mat(mats_data, mats_rows, mats_cols,
				mats_cvtype, size, mat_rows, mat_cols, mat_cvtype);

		byte[] mat_data = p_data.getByteArray(0, mat_rows[0] * mat_cols[0] * CvType.channels(mat_cvtype[0]));

		Mat img = new Mat(mat_rows[0], mat_cols[0], mat_cvtype[0]);
		img.put(0, 0, mat_data);
		// free the data memory in dll
		StitcherItf.instanceDll.free_img(p_data);

		return img;
	}

	/**
	 * 调用拼接器动态链接库 显示 Mat
	 * 
	 * @param image
	 */
	public static void showImg(Mat image) {
		byte buff[] = new byte[(int) (image.total() * image.channels())];
		image.get(0, 0, buff);
		StitcherItf.instanceDll.show_img(buff, image.rows(), image.cols(), image.type());
	}
}