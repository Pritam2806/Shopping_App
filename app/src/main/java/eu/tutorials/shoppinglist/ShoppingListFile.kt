package eu.tutorials.shoppinglist

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class ShoppingListItemClass (
    val id : Int = 0,
    val name : String = "",
    val quantity : String = "",
    val isEditing : Boolean = false,    // whether we are editing the ShoppingListItem or not
    var key: String? = null // Firebase unique key for updates/deletes
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun shoppingList (
    modifier : Modifier = Modifier,
    onButtonClicked: () -> Unit,
    emailAuth : EmailPassword
)  {

    var sItems by remember { mutableStateOf(listOf<ShoppingListItemClass>()) }   // List of Shopping items
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }

    var showDeleteDialog by remember { mutableStateOf(false) }

    val database = remember { Realtime_Database() }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Load items from Firebase on first launch
    LaunchedEffect(Unit) {
        database.getItems { items ->
            sItems = items
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        drawerContent = {
            Surface(                           // Surface to apply background and width
                modifier = Modifier
                    .width(240.dp)            // Set drawer width
                    .fillMaxHeight(),
                color = MaterialTheme.colorScheme.background     // Set drawer background color
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.shopping_icon_1_),
                            modifier = Modifier
                                .size(40.dp),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "ShoppingList", fontSize = 24.sp, style = MaterialTheme.typography.displayLarge)
                    }

                    Divider(color = Color.Gray, thickness = 2.dp, modifier = Modifier.padding(vertical = 4.dp))
                    
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Person, tint = MaterialTheme.colorScheme.onBackground, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                emailAuth.signOut()
                                scope.launch {
                                    drawerState.close()
                                }
                                onButtonClicked()
                            }
                        ) {
                            Text("Sign Out", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.displayMedium)
                        }
                    }

                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Delete, tint = MaterialTheme.colorScheme.onBackground, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                showDeleteDialog = true
                            }
                        ) {
                            Text("Delete Account", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.displayMedium)
                        }
                    }

                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Share, tint = MaterialTheme.colorScheme.onBackground, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                val githubUrl = "https://github.com/Pritam2806"  // Replace with your actual GitHub profile URL
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "Check out my GitHub profile: $githubUrl")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share GitHub Profile via"))
                            }
                        ) {
                            Text("Share GitHub", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.displayMedium)
                        }
                    }

                    Divider(color = Color.Gray, thickness = 2.dp, modifier = Modifier.padding(vertical = 4.dp))

                    Text("App Created By - \nPritam Singh Shekhawat", lineHeight = 20.sp, color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.displaySmall)

                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                showDeleteDialog = false
                            },
                            title = { Text("Delete Account") },
                            text = { Text("Are you sure you want to delete your account and all your shopping data? This action cannot be undone.") },
                            confirmButton = {
                                TextButton(onClick = {
                                    database.deleteAllUserData { success ->
                                        if (success) {
                                            emailAuth.deleteUser()
                                            scope.launch { drawerState.close() }
                                            onButtonClicked()
                                        }
                                        else {
                                            Toast.makeText(context, "Failed to delete user data", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    showDeleteDialog = false
                                }) {
                                    Text("Yes")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "My Shopping List",
                                color = Color.White,
                                style = MaterialTheme.typography.displayLarge,
                                //color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = colorResource(id = R.color.Background)
                    ),
                    navigationIcon = {
                        IconButton(
                            onClick = {         // On the top Left Side
                                // Open the drawer
                                scope.launch {
                                    drawerState.open()           // drawerState is part of scaffold
                                }    // onclick par open the drawer
                            }
                        ) {
                            Icon(imageVector = Icons.Default.AccountCircle, tint = Color.White, contentDescription = null)
                        }
                    }
                )
            },
            bottomBar = {
                Button(
                    onClick = { showDialog = true },     // Alert box khul jaayega
                    modifier = Modifier
                        .padding(bottom = 12.dp, start = 32.dp, end = 32.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        "Add Item",
                        fontSize = 18.sp
                        // modifier = Modifier.align(Alignment.Top)    // Yeh text ko upar kar dega inside button
                    )
                }
            },
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize(),     // Aise bhi pass kar sakte hai
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                // LazyColumns are used to create column of variable size.(Used to display long lists without overwhelming the system
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    items(sItems) {
                        // sItems is a state list(Hence will automatically update our user interface as soon as items inside of that list are changed)
                        // 'it' is this shopping item we get [ 'it' is the current item that we are looking at ]
                        // "items" ke andar hamesha list hi aati hai

                        // We will change the name of 'it' to 'item'
                            item ->      // Current item
                        if (item.isEditing) {      // ***** Editing window will be Opened *****
                            ShoppingItemEditor(
                                item = item,    // (it ki jagah jo 'item' banaya hai, woh hai RHS mein)
                                onEditComplete = { editedName, editedQuantity ->
                                    sItems =
                                        sItems.map { it.copy(isEditing = false) }    // Don't edit anymore
                                    // Sabka isEditing becomes false.
                                    val editedItem = sItems.find { it.id == item.id }
                                    // ***** Finding the particular item inside the list that we are editing *****
//                                    editedItem?.let {
//                                        it.name =
//                                            editedName     // Values updated(Jo humne editing window mein daali hai)
//                                        it.quantity = editedQuantity
//                                    }
                                    editedItem?.let {
                                        val updatedItem = it.copy(name = editedName, quantity = editedQuantity, isEditing = false)
                                        database.updateItem(updatedItem)
                                    }
                                })
                        } else {     // ***** When the editing window is closed *****
                            ShoppingListItem(
                                item = item,     // (it ki jagah jo 'item' banaya hai, woh hai RHS mein)
                                onEditClick = {    // [ IconButton(onClick = onEditClick ) ] // what will onEditClick function will do
                                    // *********************************************************************************
                                    // *********************************************************************************
                                    // Finding out which item we are editing and changing the "isEditing boolean" to true
                                    sItems = sItems.map { it.copy(isEditing = (it.id == item.id)) }
                                    // Jaise hi isEditing becomes true, shoppingItemEditor Function is called
                                    // Jis Box ka "EditButton" Dabaaya hoga, uske liye isEditing becomes true
                                },
                                onDeleteClick = {  // [ IconButton(onClick = onDeleteClick ) ] // what will onDeleteClick function will do
                                    database.deleteItemById(item.id)
                                    //sItems = sItems - item  // (it ki jagah jo 'item' banaya hai, woh hai RHS mein)
                                }
                            )
                        }
                    }
                }
            }
            // Column ke bahar hai hum
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },     // Alert box closed
                    confirmButton = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(onClick = {
                                if (itemName.isNotBlank() && itemQuantity.isNotBlank()) {
                                    val newItem = ShoppingListItemClass(    // Object creation
                                        id = System.currentTimeMillis().toInt(), // Basically the serial number
                                        name = itemName,
                                        quantity = itemQuantity//.toInt()
                                        // isEditing is already false(Default arguement)
                                    )
                                    database.addItem(newItem)
                                    // Added above Shopping Item
                                    showDialog = false     // Chupa de alert box ko(Add dabaane ke baad)
                                    itemName = ""    // Waapis se empty kar diya
                                    itemQuantity = ""   // Nahi toh puraani values padi rahengi
                                }
                            }   // onClick ends here
                            ) {
                                Text("Add")
                            }
                            Button(onClick = { showDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    },
                    title = { Text("Add Shopping item") },
                    text = {
                        Column(modifier) {
                            OutlinedTextField(
                                value = itemName,
                                onValueChange = { itemName = it },   // Very Important
                                singleLine = true,
                                label = { Text("Add item name") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = itemQuantity,
                                onValueChange = { itemQuantity = it },   // Very Important
                                singleLine = true,
                                label = { Text("Add item Quantity") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun ShoppingListItem (item : ShoppingListItemClass, onEditClick : () -> Unit, onDeleteClick : () -> Unit)  {
    // item is of data-type ShoppingListItemClass
    // ShoppingListItem has functions as the parameters
    // onEditClick and onDeleteClick are the lambda functions (Both don't take any parameter and doesn't return value)
    // Lambda functions are very short form functions[ onClick Function of Button is a Lambda function too ]
    // Lambda functions ke andar hum on-the-spot apna logic likh sakte hai(Apna function body likh sakte hai)
    // Hence hum apna onClick bana rahe, and we will write logic when this (Lambda)function is called

    Row (modifier = Modifier     // Hum sabse pehle dabba banaayege for storing the elements
        .padding(top = 8.dp)
        .fillMaxWidth()          // .border has two parameters(border and shape)
        .border(border = BorderStroke(2.dp, (MaterialTheme.colorScheme.onPrimaryContainer)), shape = RoundedCornerShape(20)),
    ){
        Text(text = item.name, modifier = Modifier
            .padding(start = 12.dp)
            .align(Alignment.CenterVertically)
            .weight(1f))

        Text(text = "Qty : ${item.quantity}", modifier = Modifier
            .align(Alignment.CenterVertically)
            .width(92.dp))
        // Text(text = "Qty : ${item.quantity}", modifier = Modifier.align(Alignment.CenterVertically).weight(1f))

        Row (modifier = Modifier.padding(8.dp)){    // Row inside the row
            // Row will contain two icon buttons
            // onEditclick and onDeleteclick are the parameters of this function only(Unhi parameters ko use kar rahe)
            IconButton(onClick = onEditClick ) {    // onEditclick is a lambda function
                // onClick par onEditClick ki body execute hogi.
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
            IconButton(onClick = onDeleteClick ) {  // onDeleteclick is a lambda function
                // onClick par onDeleteClick ki body execute hogi.
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
    }
}

@Composable
fun ShoppingItemEditor (item : ShoppingListItemClass, onEditComplete : (String, String) -> Unit)  {
    // onEditComplete is a Lambda function(Takes a 'string' and 'Integer' as the input and returns nothing
    var editedName by remember { mutableStateOf(item.name) }   // By default 'current item' ka name hai
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }  // By default 'current item' ki quantity hai
    // Editing window mein kaunsi cheeze by default likhi hongi
    var isEditing by remember { mutableStateOf(item.isEditing) }     // ****** By default false hoga ******

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(border = BorderStroke(2.dp, (MaterialTheme.colorScheme.onPrimaryContainer)), shape = RoundedCornerShape(20))
            .background(color = MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column (
            modifier = Modifier.padding(start = 4.dp)
        ) {     // Breaking the convention
            Row {
                Text("Item Name",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 2.dp)
                        .wrapContentSize()
                )
                Spacer(modifier = Modifier.width(20.dp))
                BasicTextField(
                    value = editedName,
                    onValueChange = { editedName = it },     // Very Important step(To reflect the typings in field)
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp),
                    singleLine = true,
                    modifier = Modifier
                        //.wrapContentSize()
                        .padding(
                            start = 24.dp,
                            end = 8.dp,
                            bottom = 3.dp,
                            top = 7.dp,
                        )  // Takes the required space only
                        .width(140.dp)
                )
            }
            Row {
                Text("Item Quantity",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(bottom = 8.dp, start = 8.dp, end = 8.dp, top = 2.dp)
                        .wrapContentSize()
                )

                BasicTextField(
                    value = editedQuantity,
                    onValueChange = { editedQuantity = it },     // Very Important step(To reflect the typings in field)
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp),
                    singleLine = true,
                    modifier = Modifier
                        //.wrapContentSize()
                        .padding(
                            start = 24.dp,
                            end = 8.dp,
                            top = 6.dp,
                            bottom = 8.dp
                        )  // Takes the required space only
                        .width(140.dp)
                )
            }
        }     // Column ends here
        IconButton(
            onClick = {
                isEditing = false     // function ki body mein hai, hence no ',' comma at last
                // Editing window band ho jaayegi[ Reason -> if (item.isEditing)  {...} ]Line no - 73(around)
                onEditComplete(editedName, editedQuantity)   // null par 1 ho jaayegi value
                // ****** onEditComplete are the parameters of this function only(Unhi parameters ko use kar rahe)
                // ****** editedQuantity is a string here (Usko int banana padega) ******
                // ****** Value bhi update kardi hai ******
                // ****** onClick par humne onEditClick function call kardiya hai ******
            },
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}