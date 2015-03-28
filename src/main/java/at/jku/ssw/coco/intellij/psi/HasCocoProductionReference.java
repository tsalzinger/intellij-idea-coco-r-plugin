package at.jku.ssw.coco.intellij.psi;

import com.intellij.psi.ContributedReferenceHost;
import org.jetbrains.annotations.Nullable;

public interface HasCocoProductionReference extends ContributedReferenceHost, HasIdent {
    @Nullable
    String getProductionReferenceName();
}