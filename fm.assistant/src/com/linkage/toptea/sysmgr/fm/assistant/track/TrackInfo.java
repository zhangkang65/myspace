package com.linkage.toptea.sysmgr.fm.assistant.track;
/**
 * ���ܣ�������
 * ���ߣ��˻�
 * ʱ�䣺20110214
 */
public class TrackInfo {
	
	//�澯���
	private String alarmId;
	//���ٱ��
	private String id;
	//������
	private String tracker;
	//���ټ�¼
	private String content;
	//����ʱ��
	private long trackdate;
	//��������:1������ 2���ӳ�
	private int type = 1;

	public String getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTracker() {
		return tracker;
	}

	public void setTracker(String tracker) {
		this.tracker = tracker;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getTrackdate() {
		return trackdate;
	}

	public void setTrackdate(long trackdate) {
		this.trackdate = trackdate;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
