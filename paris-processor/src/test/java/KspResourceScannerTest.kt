import com.airbnb.paris.processor.android_resource_scanner.KspResourceScanner
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class KspResourceScannerTest  {
    @Test
    fun findMatchingImportPackage_TypeAlias() {
        val import = KspResourceScanner.findMatchingImportPackage(
            importedNames = listOf("com.airbnb.paris.test.R2 as typeAliasedR"),
            annotationReference = "typeAliasedR.layout.my_layout",
            annotationReferencePrefix = "typeAliasedR",
            packageName = "com.airbnb.paris"
        )

        expectThat(import.fullyQualifiedReference).isEqualTo("com.airbnb.paris.test.R2.layout.my_layout")
    }

    @Test
    fun findMatchingImportPackage_TypeAliasDoesNotMatch() {
        val import = KspResourceScanner.findMatchingImportPackage(
            importedNames = listOf("com.airbnb.paris.test.R2 as typeAliasedR2"),
            annotationReference = "typeAliasedR.layout.my_layout",
            annotationReferencePrefix = "typeAliasedR",
            packageName = "com.airbnb.paris"
        )

        // falls back to annotation reference, since import should not match
        expectThat(import.fullyQualifiedReference).isEqualTo("typeAliasedR.layout.my_layout")
    }

    @Test
    fun findMatchingImportPackage_fullyStaticImport() {
        val import = KspResourceScanner.findMatchingImportPackage(
            importedNames = listOf("com.airbnb.n2.comp.designsystem.hostdls.R2.styleable.n2_CarouselCheckedActionCard_n2_layoutStyle"),
            annotationReference = "n2_CarouselCheckedActionCard_n2_layoutStyle",
            annotationReferencePrefix = "n2_CarouselCheckedActionCard_n2_layoutStyle",
            packageName = "com.airbnb.n2.comp.designsystem.hostdls"
        )

        // falls back to annotation reference, since import should not match
        expectThat(import.fullyQualifiedReference).isEqualTo("com.airbnb.n2.comp.designsystem.hostdls.R2.styleable.n2_CarouselCheckedActionCard_n2_layoutStyle")
    }

}