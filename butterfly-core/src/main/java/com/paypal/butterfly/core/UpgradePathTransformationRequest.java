package com.paypal.butterfly.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;
import com.paypal.butterfly.api.Application;
import com.paypal.butterfly.api.Configuration;

import java.io.File;
import java.io.IOException;

/**
 * Represents an specific transformation, made of an
 * {@link com.paypal.butterfly.extensions.api.upgrade.UpgradePath},
 * to be applied against a specific application
 *
 * @author facarvalho
 */
@SuppressWarnings("PMD.DefaultPackage")
class UpgradePathTransformationRequest extends AbstractTransformationRequest {

    // The upgrade path to be applied
    private transient UpgradePath upgradePath;
    private transient static Gson gson;

    UpgradePathTransformationRequest(Application application, UpgradePath upgradePath, Configuration configuration) {
        super(application, configuration, false);
        if (upgradePath == null) {
            throw new IllegalArgumentException("Upgrade path cannot be null");
        }
        this.upgradePath = upgradePath;

        extensionName = getExtensionName(upgradePath.getExtension());
        extensionVersion = getExtensionVersion(upgradePath.getExtension());
        templateName = upgradePath.getFirstStepTemplateName();
        templateClassName = upgradePath.getFirstStepTemplateClassName();
        upgradeStep = true;
    }

    UpgradePath getUpgradePath() {
        return upgradePath;
    }

    private String toJson() {
        if (gson == null) {
            gson = new GsonBuilder().serializeNulls().setPrettyPrinting().registerTypeAdapter(File.class, new TypeAdapter<File>() {
                @Override
                public void write(JsonWriter jsonWriter, File file) throws IOException {
                    String fileAbsolutePath = (file == null ? null : file.getAbsolutePath());
                    jsonWriter.value(fileAbsolutePath);
                }
                @Override
                public File read(JsonReader jsonReader) {
                    throw new UnsupportedOperationException("There is no support for deserializing UpgradePathTransformationRequest objects at the moment");
                }
            }).create();
        }
        return gson.toJson(this);
    }

    @Override
    public String toString() {
        return toJson();
    }

}
