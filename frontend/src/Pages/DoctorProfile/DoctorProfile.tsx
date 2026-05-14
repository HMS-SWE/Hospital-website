import Input from '../../Components/Input/input'
import Styles from './DoctorProfile.module.css'
import { useState, useEffect } from 'react' 


interface DoctorProfileData{
    username: string;
    fullName: string;
    department: string;
    degree: string;
    speciality: string;
    emergencyContact: string;
    examinationPrice: string;
    profileImageUrl?: string;
}
function DoctorProfile(){
    const [profileData, setProfileData] = useState<DoctorProfileData | null>(null);
    const [phone, setPhone] = useState("");
    const [price, setPrice] = useState("");
    const [profileImage, setProfileImage] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [saving, setSaving] = useState(false);

    
    useEffect(() => {
    const fetchProfile = async () => {
      try {
        setLoading(true);
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        const token = localStorage.getItem('token');
        const response = await fetch(`http://localhost:8080/api/doctors/${user.id}/profile`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        });

        if (!response.ok) throw new Error('Failed to fetch profile');

        const data: DoctorProfileData = await response.json();
        console.log('API response: ', data);
        setProfileData(data);

        // Seed editable fields
        setPhone(data.emergencyContact || "");
        setPrice(data.examinationPrice || "");
        if (data.profileImageUrl) setProfileImage(data.profileImageUrl);

      } catch (err) {
        setError(err instanceof Error ? err.message : 'An error occurred');
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, []);
  const handleImageChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    // Optimistic local preview
    setProfileImage(URL.createObjectURL(file));

    try {
      const token = localStorage.getItem('token');
      const formData = new FormData();
      formData.append('profileImage', file);

      const response = await fetch('/api/profile/image', {
        method: 'PUT',
        headers: { 'Authorization': `Bearer ${token}` },
        body: formData,
      });

      if (!response.ok) throw new Error('Image upload failed');

    } catch (err) {
      setError('Failed to upload image');
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
  e.preventDefault();

  try {
    setSaving(true);

    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('user') || '{}');

    const response = await fetch(`http://localhost:8080/api/doctors/${user.id}/profile`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        emergencyContact: phone,
        examinationPrice: price,
      }),
    });

    if (!response.ok) {
      throw new Error('Failed to save changes');
    }

    const updated: DoctorProfileData = await response.json();

    setProfileData(updated);

    alert('Changes saved successfully!');

  } catch (err) {
    setError(err instanceof Error ? err.message : 'Save failed');
  } finally {
    setSaving(false);
  }
};
  const handleCancel = () => {
    if (profileData) {
      setPhone(profileData.emergencyContact || "");
      setPrice(profileData.examinationPrice || "");
    }
  };

  if (loading) return <div>Loading profile...</div>;
  if (error)   return <div>Error: {error}</div>;
  if (!profileData) return <div>No profile data found</div>;


    return(
        <>
        <div className={Styles.dProfileContainer}>
            <div className={Styles.dProfileContent}>
                <div className={Styles.dprofile}>
                    <input
                        type="file"
                        accept="image/*"
                        id="profile-upload"
                        style={{ display: 'none' }}
                        onChange={handleImageChange}
                    />
                    <label htmlFor="profile-upload" className={Styles.changeImg}>
                        Change Image
                    </label>
                    <img src={profileImage || "https://via.placeholder.com/150"} alt='ProfileImg' ></img>
                    <h1>{profileData.username}</h1>
                </div>
                <h2>Personal Info:</h2>
                <div className={Styles.dPersonal}>
                    <Input 
                        label='Full Name:'
                        value={profileData.fullName}
                        type='text'
                        />
                    <Input 
                        label='Department:'
                        value={profileData.department}
                        type='text'/>
                    <Input 
                        label='Degree:'
                        value={profileData.degree}
                        type='text'/>
                    <Input 
                        label='Speciality:'
                        value={profileData.speciality}
                        type='text'/>

                </div>
                <div className={Styles.divider}></div>
                <h2>Professional details:</h2>
                <form onSubmit={handleSubmit}>
                    <div className={Styles.profDetails}>
                        <Input 
                            label="Emergency Contact:*"
                            value={phone}
                            type="text"
                            onChange={(e) => setPhone(e.target.value)}
                        />
                        <Input 
                            label="Examination Price:*"
                            value={price}
                            type="text"
                            onChange={(e) => setPrice(e.target.value)}
                        />
                    </div>
                    <div className={Styles.dProfileButtons}>
                            <button type="submit" className={Styles.saveButton} disabled={saving}>{saving ? 'Saving...' : 'Save Changes'}</button>
                            <button type="button" className={Styles.cancelButton} onClick={handleCancel}>Cancel</button>
                    </div>
                </form>

            </div>
        </div>
        </>
    );
}
export default DoctorProfile;