import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.notemanagerapp.LoginActivity
import com.example.notemanagerapp.R
import com.example.notemanagerapp.DBHelper
import com.example.notemanagerapp.EditProfileActivity

class ProfileFragment : Fragment() {
    private lateinit var dbHelper: DBHelper
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private var username: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        dbHelper = DBHelper(requireContext())

        usernameTextView = view.findViewById(R.id.textViewUsername)
        emailTextView = view.findViewById(R.id.textViewEmail)
        val logoutButton = view.findViewById<Button>(R.id.buttonLogout)
        val deleteAccountButton = view.findViewById<Button>(R.id.buttonDeleteAccount)
        val editDataButton = view.findViewById<Button>(R.id.buttonEditData)

        username = arguments?.getString("USERNAME") ?: ""
        
        loadUserData()

        deleteAccountButton.setOnClickListener {
            if (username.isNotEmpty()) {
                val currentUserData = dbHelper.getUserData(username)
                if (currentUserData != null) {
                    val isDeleted = dbHelper.deleteUser(currentUserData.first)
                    if (isDeleted) {
                        Toast.makeText(requireContext(), "Account deleted", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireActivity(), LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(requireContext(), "Error deleting account", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                }
            }
        }

        logoutButton.setOnClickListener {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        editDataButton.setOnClickListener {
            val intent = Intent(requireActivity(), EditProfileActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivityForResult(intent, EDIT_PROFILE_REQUEST)
        }

        return view
    }

    private fun loadUserData() {
        val userData = dbHelper.getUserData(username)
        if (userData != null) {
            username = userData.first
            usernameTextView.text = "Username: ${userData.first}"
            emailTextView.text = "Email: ${userData.second}"
        } else {
            usernameTextView.text = "Username: Not found"
            emailTextView.text = "Email: Not found"
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == Activity.RESULT_OK) {
            username = data?.getStringExtra("NEW_USERNAME") ?: username
            loadUserData()
        }
    }

    companion object {
        private const val EDIT_PROFILE_REQUEST = 1
    }
}

