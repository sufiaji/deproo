package com.deproo.android.deproo.model;

import com.deproo.android.deproo.utils.Constants;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName(Constants.ParseTable.TableAssetImage.NAME)
public class AssetImage extends ParseObject {

    public AssetImage() {
        super();
    }

    public void setImageFile(ParseFile file) {
        put(Constants.ParseTable.TableAssetImage.IMAGE_FILE, file);
    }

    public ParseFile getImageFile() {
        return getParseFile(Constants.ParseTable.TableAssetImage.IMAGE_FILE);
    }

    public ParseFile getImageThumb() {
        return getParseFile(Constants.ParseTable.TableAssetImage.IMAGE_THUMB);
    }

    public void setOwner(Asset asset) {
        put(Constants.ParseTable.TableAssetImage.ASSET, asset);
    }

    public Asset getOwner() {
        return (Asset) getParseObject(Constants.ParseTable.TableAssetImage.ASSET);
    }

    public void setFilename(String filename) {
        put(Constants.ParseTable.TableAssetImage.IMAGE_FILENAME, filename);
    }

    public String getFilename() {
        return getString(Constants.ParseTable.TableAssetImage.IMAGE_FILENAME);
    }

    public void setAliasName(String filename) {
        put(Constants.ParseTable.TableAssetImage.IMAGE_ALIASNAME, filename);
    }

    public String getAliasName() {
        return getString(Constants.ParseTable.TableAssetImage.IMAGE_ALIASNAME);
    }

    public void setThumbFile(ParseFile parseFile) {
        put(Constants.ParseTable.TableAssetImage.IMAGE_THUMB, parseFile);
    }

    public ParseFile getThumbfile() {
        return getParseFile(Constants.ParseTable.TableAssetImage.IMAGE_THUMB);
    }
}
