package implementation;


import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface StitcherItf extends Library{
	StitcherItf instanceDll  = (StitcherItf)Native.loadLibrary("stitcher_win_lib_project",StitcherItf.class);  
	public int stitchimg_from_path(String[] src_path,int size);
	public int stitchimg_from_path_to_path(String[] src_path,int size,String dst_path);
	public Pointer stitchimg_from_mats_to_mat(byte[] mats, int[] mats_rows, int[] mats_cols, int[] mats_cvtype, int size,
			int[] mat_rows, int[] mat_cols, int[] mat_cvtype);
	
	public int show_img(byte[] img_data,int rows,int cols,int cvtype);
	public Pointer load_img(String img_path,int[] rows,int[] cols,int[] cvtype);
	public void free_img(Pointer p_img);
}
