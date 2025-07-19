package com.d4rk.cleaner.app.contacts.cleaner.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.TonalIconButtonWithText
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R

@Composable
fun ContactsCleanerCard(modifier: Modifier = Modifier, onOpen: () -> Unit) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SizeConstants.ExtraLargeSize)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SizeConstants.LargeSize),
            verticalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Contacts,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.padding(start = SizeConstants.MediumSize)) {
                    Text(
                        text = stringResource(id = R.string.contacts_cleaner_card_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(id = R.string.contacts_cleaner_card_subtitle),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            TonalIconButtonWithText(
                label = stringResource(id = R.string.open_contacts_cleaner),
                icon = Icons.Outlined.Contacts,
                onClick = onOpen,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
