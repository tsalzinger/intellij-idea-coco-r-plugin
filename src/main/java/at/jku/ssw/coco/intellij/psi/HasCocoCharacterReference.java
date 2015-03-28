package at.jku.ssw.coco.intellij.psi;

import com.intellij.psi.ContributedReferenceHost;
import org.jetbrains.annotations.Nullable;

public interface HasCocoCharacterReference extends ContributedReferenceHost, HasIdent {
    @Nullable
    String getCharacterReferenceName();
}