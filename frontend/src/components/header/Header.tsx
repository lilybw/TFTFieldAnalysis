import React from 'react';
import './Header.css';

interface HeaderProps {
    backendVersion: String;
    frontendVersion: String;
}

const Header = ({backendVersion, frontendVersion}: HeaderProps): JSX.Element => {
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