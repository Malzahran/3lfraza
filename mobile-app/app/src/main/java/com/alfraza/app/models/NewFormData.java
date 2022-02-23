package com.alfraza.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class NewFormData implements Parcelable {
    public static final Creator<NewFormData> CREATOR = new Creator<NewFormData>() {
        @Override
        public NewFormData createFromParcel(Parcel in) {
            return new NewFormData(in);
        }

        @Override
        public NewFormData[] newArray(int size) {
            return new NewFormData[size];
        }
    };
    private String req_msg, loc_msg, file_msg, photo_msg;
    private String formhead, forminst, formdesc, formprice, formbutton, pre_bottom_notes, bottom_notes, files_head, location_title, up_file_btn, up_img_btn, change_file_btn, change_img_btn, image_url;
    private int camera, uploadimg, uploadfile, uploadimgcount, uploadfilecount, location;
    private FormInputData[] formdata;
    private IntentData intentData;

    private NewFormData(Parcel in) {
        req_msg = in.readString();
        loc_msg = in.readString();
        file_msg = in.readString();
        photo_msg = in.readString();
        formhead = in.readString();
        forminst = in.readString();
        formdesc = in.readString();
        formprice = in.readString();
        formbutton = in.readString();
        pre_bottom_notes = in.readString();
        bottom_notes = in.readString();
        location_title = in.readString();
        files_head = in.readString();
        up_file_btn = in.readString();
        up_img_btn = in.readString();
        change_file_btn = in.readString();
        change_img_btn = in.readString();
        image_url = in.readString();
        camera = in.readInt();
        uploadimg = in.readInt();
        uploadfile = in.readInt();
        uploadimgcount = in.readInt();
        uploadfilecount = in.readInt();
        location = in.readInt();
        formdata = in.createTypedArray(FormInputData.CREATOR);
        intentData = in.readParcelable(IntentData.class.getClassLoader());
    }

    public int CaptureImg() {
        return camera;
    }

    public int UploadImg() {
        return uploadimg;
    }

    public int UploadFile() {
        return uploadfile;
    }

    public int UploadImgCount() {
        return uploadimgcount;
    }

    public int UploadFileCount() {
        return uploadfilecount;
    }

    public int AllowLocation() {
        return location;
    }

    public String getFormhead() {
        return formhead;
    }

    public String getFormprice() {
        return formprice;
    }

    public String getForminst() {
        return forminst;
    }

    public String getFormdesc() {
        return formdesc;
    }

    public String getFormimage() {
        return image_url;
    }

    public String getFormbutton() {
        return formbutton;
    }

    public String getPreBottomNotes() {
        return pre_bottom_notes;
    }

    public String getBottomNotes() {
        return bottom_notes;
    }

    public String getLocation_title() {
        return location_title;
    }

    public String getFiles_head() {
        return files_head;
    }

    public String getReqMsg() {
        return req_msg;
    }

    public String getLocMsg() {
        return loc_msg;
    }

    public String getFileMsg() {
        return file_msg;
    }

    public String getPhotoMsg() {
        return photo_msg;
    }

    public String getUp_file_btn() {
        return up_file_btn;
    }

    public String getUp_img_btn() {
        return up_img_btn;
    }

    public String getChange_file_btn() {
        return change_file_btn;
    }

    public String getChange_img_btn() {
        return change_img_btn;
    }

    public IntentData getIntent() {
        return intentData;
    }

    public FormInputData[] getFormInputs() {
        return formdata;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(req_msg);
        dest.writeString(loc_msg);
        dest.writeString(file_msg);
        dest.writeString(photo_msg);
        dest.writeString(formhead);
        dest.writeString(forminst);
        dest.writeString(formdesc);
        dest.writeString(formprice);
        dest.writeString(formbutton);
        dest.writeString(pre_bottom_notes);
        dest.writeString(bottom_notes);
        dest.writeString(location_title);
        dest.writeString(files_head);
        dest.writeString(up_file_btn);
        dest.writeString(up_img_btn);
        dest.writeString(change_file_btn);
        dest.writeString(change_img_btn);
        dest.writeString(image_url);
        dest.writeInt(camera);
        dest.writeInt(uploadimg);
        dest.writeInt(uploadfile);
        dest.writeInt(uploadimgcount);
        dest.writeInt(uploadfilecount);
        dest.writeInt(location);
        dest.writeTypedArray(formdata, flags);
        dest.writeParcelable(intentData, flags);
    }
}