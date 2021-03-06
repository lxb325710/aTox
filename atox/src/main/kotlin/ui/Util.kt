package ltd.evilcorp.atox.ui

import android.content.res.Resources
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import ltd.evilcorp.atox.R
import ltd.evilcorp.core.vo.ConnectionStatus
import ltd.evilcorp.core.vo.Contact
import ltd.evilcorp.core.vo.UserStatus

internal fun colorByStatus(resources: Resources, contact: Contact): Int {
    if (contact.connectionStatus == ConnectionStatus.None) return ResourcesCompat.getColor(
        resources,
        R.color.statusOffline,
        null
    )
    return when (contact.status) {
        UserStatus.None -> ResourcesCompat.getColor(resources, R.color.statusAvailable, null)
        UserStatus.Away -> ResourcesCompat.getColor(resources, R.color.statusAway, null)
        UserStatus.Busy -> ResourcesCompat.getColor(resources, R.color.statusBusy, null)
    }
}

internal fun setAvatarFromContact(imageView: ImageView, contact: Contact) =
    if (contact.avatarUri.isNotEmpty()) {
        imageView.setImageURI(Uri.parse(contact.avatarUri))
    } else {
        // TODO(robinlinden): Nicer default image. Probably something generated from the public key.
        imageView.setImageResource(R.mipmap.ic_launcher)
    }
