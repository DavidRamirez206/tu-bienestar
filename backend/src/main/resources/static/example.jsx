import { useEffect, useState } from "react";
import axios from 'axios';

//using some hooks for fun.
function UserList(){
    const [users, setUsers] = useState([]);

    useEffect(() => {
        axios.get('/users')
            .then(response => setUsers(response.data))
            .catch(error => console.error(error));
    }, []);

    return(
        <div>
            <h1>Users</h1>
            <ul>
                {users.map(user => (
                    <li key={user.id}>{user.name}</li>
                ))}
            </ul>
        </div>
    )
}
export default UserList;