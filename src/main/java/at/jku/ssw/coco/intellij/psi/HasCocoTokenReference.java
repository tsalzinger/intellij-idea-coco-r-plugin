package at.jku.ssw.coco.intellij.psi;

import com.intellij.psi.ContributedReferenceHost;
import org.jetbrains.annotations.Nullable;

public interface HasCocoTokenReference extends ContributedReferenceHost, HasIdent {
    @Nullable
    String getTokenReferenceName();
}