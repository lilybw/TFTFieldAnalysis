import React from 'react';
import './Header.css';
import { getBackendVersion } from '../../ts/backendIntegration'

interface HeaderProps {
    frontendVersion: String;
}

const Header = ({ frontendVersion}: HeaderProps): JSX.Element => {
    const [backendVersion, setBackendVersion] = React.useState("Unavailable")

    React.useEffect(() => {
        getBackendVersion().then((version) => {
            if(version.response == null) return;
            setBackendVersion(version.response)
        })
    }, [])

    return (
        <div className="Header">
            <h1>TFTFA</h1>
            <div className="version">
                <p>Frontend: {frontendVersion}</p>
                <p>Backend: {backendVersion}</p>
            </div>
        </div>
    );
}
export default Header;