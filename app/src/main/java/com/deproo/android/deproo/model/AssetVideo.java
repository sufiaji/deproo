package com.deproo.android.deproo.model;

import com.deproo.android.deproo.utils.Constants;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName(Constants.ParseTable.TableAssetVideo.NAME)
public class AssetVideo extends ParseObject {

    public AssetVideo() {
        super();
    }

    public void setVideoFile(ParseFile file) {
        put(Constants.ParseTable.TableAssetVideo.VIDEO_FILE, file);
    }

    public ParseFile getVideoFile() {
        return getParseFile(Constants.ParseTable.TableAssetVideo.VIDEO_FILE);
    }

    public void setOwner(ParseObject asset) {
        put(Constants.ParseTable.TableAssetVideo.ASSET, asset);
    }

    public ParseObject getOwner() {
        return getParseObject(Constants.ParseTable.TableAssetVideo.ASSET);
    }

    public void setFilename(String filename) {
        put(Constants.ParseTable.TableAssetVideo.VIDEO_FILENAME, filename);
    }

    public String getFilename() {
        return getString(Constants.ParseTable.TableAssetVideo.VIDEO_FILENAME);
    }

    public void setAliasName(String filename) {
        put(Constants.ParseTable.TableAssetVideo.VIDEO_ALIASNAME, filename);
    }

    public String getAliasName() {
        return getString(Constants.ParseTable.TableAssetVideo.VIDEO_ALIASNAME);
    }

    public void setThumbFile(ParseFile parseFile) {
        put(Constants.ParseTable.TableAssetVideo.VIDEO_THUMB, parseFile);
    }

    public ParseFile getThumbfile() {
        return getParseFile(Constants.ParseTable.TableAssetVideo.VIDEO_THUMB);
    }
}
