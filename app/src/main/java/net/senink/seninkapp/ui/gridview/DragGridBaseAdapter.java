package net.senink.seninkapp.ui.gridview;

public interface DragGridBaseAdapter {
	/**
	 * �����������
	 * @param oldPosition
	 * @param newPosition
	 */
	public void reorderItems(int oldPosition, int newPosition);
	
	
	/**
	 * ����ĳ��item����
	 * @param hidePosition
	 */
	public void setHideItem(int hidePosition);
	
	/**
	 * ɾ��ĳ��item
	 * @param removePosition
	 */
	public void removeItem(int removePosition);
	

}
